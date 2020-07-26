package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PasswordTest {
    @Test
    public void testHash1() {
        String hashed = Password.hash("password");
        assertEquals(128, hashed.length());
        assertEquals("8f00b8b023b45e36d8a1c1f353acaf1e6accac1a2421e2a59fb57fbfdb9c2cbc5f64f83246dcd87a5ad86be85332c72fa50a9514572bca020994222a438ddb46", hashed);
    }

    @Test
    public void testHash2() {
        String hashed = Password.hash("password123");
        assertEquals(128, hashed.length());
        assertEquals("948290aa49e8ea463439f77fb7ee67b3f9bd14a8cd076d9437421e9d914d0fccbccdca2af31a234a96c9a0df3e5ace50b8efd39f0948f5a346a0a7c7e1bb7fea", hashed);
    }

    @Test
    public void testHash3() {
        String hashed = Password.hash("whatever");
        assertEquals(128, hashed.length());
        assertEquals("194027eb75ed10b90c46eb1dfc6e5e46fb4d522268a46ca0ceae9d632be513f8059d30b8556878eba4b3f51f756bf45416995a4b1534f0041d19f38bac4fe9b7", hashed);
    }
}
