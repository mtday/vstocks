package vstocks.rest.security;

import org.junit.Test;
import org.pac4j.core.config.Config;

import static org.junit.Assert.assertFalse;

public class SecurityConfigTest {
    @Test
    public void test() {
        Config config = SecurityConfig.getConfig();
        assertFalse(config.getClients().getClients().isEmpty());
    }
}
