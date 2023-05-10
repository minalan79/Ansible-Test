import jenkins.model.Jenkins
import hudson.security.*
import jenkins.security.s2m.AdminWhitelistRule
import jenkins.security.seed.UserSeedProperty

def instance = Jenkins.getInstance()

// Disable security setup wizard
instance.setInstallState(InstallState.INITIAL_SETUP_COMPLETED)

// Create the admin user
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount('admin', 'admin_password')

// Assign the admin role to the admin user
def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Permission.IDEA, 'admin')
instance.setAuthorizationStrategy(strategy)

// Save the security configurations
instance.setSecurityRealm(hudsonRealm)
instance.save()

// Enable the seed user property
instance.getAllItems().each {
    def prop = it.getProperty(UserSeedProperty.class)
    if (prop != null) {
        prop.setSeedEnabled(true)
        prop.save()
    }
}

// Whitelist the AdminWhitelistRule
AdminWhitelistRule rule = new AdminWhitelistRule(
    ['authenticated': 'authenticated', 'admin': 'admin']
)
rule.save()
