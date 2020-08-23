package vstocks.db;

public class UserServiceImplIT extends BaseServiceImplIT {
    /*
    private UserDB userTable;
    private UserCreditsDB userCreditsTable;
    private StockDB stockTable;
    private StockPriceDB stockPriceTable;
    private UserStockDB userStockTable;
    private ActivityLogTable activityLogTable;

    private UserServiceImpl userDB;
    private UserCreditsServiceImpl userCreditsDB;
    private UserStockServiceImpl userStockDB;

    @Before
    public void setup() {
        userTable = new UserDB();
        userCreditsTable = new UserCreditsDB();
        stockTable = new StockDB();
        stockPriceTable = new StockPriceDB();
        userStockTable = new UserStockDB();
        activityLogTable = new ActivityLogTable();

        userDB = new UserServiceImpl(dataSourceExternalResource.get());
        userCreditsDB = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        userStockDB = new UserStockServiceImpl(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            activityLogTable.truncate(connection);
            userStockTable.truncate(connection);
            stockTable.truncate(connection);
            stockPriceTable.truncate(connection);
            userCreditsTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testUsernameExistsMissing() {
        assertFalse(userDB.usernameExists("missing"));
    }

    @Test
    public void testUsernameExists() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userDB.add(user));
        assertTrue(userDB.usernameExists(user.getUsername()));
    }

    @Test
    public void testGetMissing() {
        assertFalse(userDB.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));

        User fetched = userDB.get(user.getId()).orElse(null);
        assertEquals(user, fetched);
    }

    @Test
    public void testGetLowercaseEmail() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));

        User fetched = userDB.get(user.getId()).orElse(null);
        assertEquals(user, fetched);
    }

    @Test
    public void testGetUppercaseEmail() {
        User user = new User().setId(generateId("USER@DOMAIN.COM")).setEmail("USER@DOMAIN.COM").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));

        user.setEmail("user@domain.com");
        User fetched = userDB.get(user.getId()).orElse(null);
        assertEquals(user, fetched);
    }

    @Test
    public void testGetNoProfileImage() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userDB.add(user));

        Optional<User> fetched = userDB.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getId(), fetched.get().getId());
        assertEquals(user.getEmail().toLowerCase(ENGLISH), fetched.get().getEmail());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        assertNull(fetched.get().getProfileImage());
    }

    @Test
    public void testGetAllNone() {
        Results<User> results =  userDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1").setProfileImage("link");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2").setProfileImage("link");
        assertEquals(1, userDB.add(user1));
        assertEquals(1, userDB.add(user2));

        Results<User> results = userDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user1, results.getResults().get(0));
        assertEquals(user2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1").setProfileImage("link");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2").setProfileImage("link");
        assertEquals(1, userDB.add(user1));
        assertEquals(1, userDB.add(user2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
        Results<User> results = userDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user2, results.getResults().get(0));
        assertEquals(user1, results.getResults().get(1));
    }

    @Test
    public void testGetAllMultiplePagesNoSort() {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1").setProfileImage("link");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2").setProfileImage("link");
        User user3 = new User().setId(generateId("user3@domain.com")).setEmail("user3@domain.com").setUsername("name3").setDisplayName("Name3").setProfileImage("link");
        User user4 = new User().setId(generateId("user4@domain.com")).setEmail("user4@domain.com").setUsername("name4").setDisplayName("Name4").setProfileImage("link");
        User user5 = new User().setId(generateId("user5@domain.com")).setEmail("user5@domain.com").setUsername("name5").setDisplayName("Name5").setProfileImage("link");
        for (User user : asList(user1, user2, user3, user4, user5)) {
            assertEquals(1, userDB.add(user));
        }

        Page page = new Page().setSize(2);
        Results<User> results = userDB.getAll(page, emptySet());
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user1, results.getResults().get(0));
        assertEquals(user2, results.getResults().get(1));

        page = page.next();
        results = userDB.getAll(page, emptySet());
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user3, results.getResults().get(0));
        assertEquals(user4, results.getResults().get(1));

        page = page.next();
        results = userDB.getAll(page, emptySet());
        assertEquals(5, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(user5, results.getResults().get(0));
    }

    @Test
    public void testGetAllMultiplePagesWithSort() {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1").setProfileImage("link");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2").setProfileImage("link");
        User user3 = new User().setId(generateId("user3@domain.com")).setEmail("user3@domain.com").setUsername("name3").setDisplayName("Name3").setProfileImage("link");
        User user4 = new User().setId(generateId("user4@domain.com")).setEmail("user4@domain.com").setUsername("name4").setDisplayName("Name4").setProfileImage("link");
        User user5 = new User().setId(generateId("user5@domain.com")).setEmail("user5@domain.com").setUsername("name5").setDisplayName("Name5").setProfileImage("link");
        for (User user : asList(user1, user2, user3, user4, user5)) {
            assertEquals(1, userDB.add(user));
        }

        Page page = new Page().setSize(2);
        Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
        Results<User> results = userDB.getAll(page, sort);
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user5, results.getResults().get(0));
        assertEquals(user4, results.getResults().get(1));

        page = page.next();
        results = userDB.getAll(page, sort);
        assertEquals(5, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(user3, results.getResults().get(0));
        assertEquals(user2, results.getResults().get(1));

        page = page.next();
        results = userDB.getAll(page, sort);
        assertEquals(5, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(user1, results.getResults().get(0));
    }

    @Test
    public void testConsumeNone() {
        List<User> results = new ArrayList<>();
        assertEquals(0, userDB.consume(results::add, emptySet()));
        assertTrue(results.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1").setProfileImage("link");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2").setProfileImage("link");
        assertEquals(1, userDB.add(user1));
        assertEquals(1, userDB.add(user2));

        List<User> results = new ArrayList<>();
        assertEquals(2, userDB.consume(results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(user1, results.get(0));
        assertEquals(user2, results.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1").setProfileImage("link");
        User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2").setProfileImage("link");
        assertEquals(1, userDB.add(user1));
        assertEquals(1, userDB.add(user2));

        List<User> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort()));
        assertEquals(2, userDB.consume(results::add, sort));
        assertEquals(2, results.size());
        assertEquals(user2, results.get(0));
        assertEquals(user1, results.get(1));
    }

    @Test
    public void testReset() throws SQLException {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userCreditsTable.update(connection, user.getId(), 1234));

            Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("Name1");
            Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("Name2");
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));

            Instant now = Instant.now().truncatedTo(SECONDS);
            StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol("sym1").setPrice(2).setTimestamp(now);
            StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol("sym2").setPrice(3).setTimestamp(now);
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));

            UserStock userStock1 = new UserStock().setUserId(user.getId()).setMarket(TWITTER).setSymbol("sym1").setShares(3);
            UserStock userStock2 = new UserStock().setUserId(user.getId()).setMarket(TWITTER).setSymbol("sym2").setShares(4);
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));

            connection.commit();
        }

        Optional<UserCredits> userCreditsBeforeReset = userCreditsDB.get(user.getId());
        assertTrue(userCreditsBeforeReset.isPresent());
        assertEquals(11234, userCreditsBeforeReset.get().getCredits());

        Results<UserStock> userStocksBeforeReset = userStockDB.getForUser(user.getId(), new Page(), emptySet());
        assertEquals(2, userStocksBeforeReset.getTotal());
        assertEquals(2, userStocksBeforeReset.getResults().size());

        userDB.reset(user.getId());

        Optional<UserCredits> userCreditsAfterReset = userCreditsDB.get(user.getId());
        assertTrue(userCreditsAfterReset.isPresent());
        assertEquals(10000, userCreditsAfterReset.get().getCredits());

        Results<UserStock> userStocksAfterReset = userStockDB.getForUser(user.getId(), new Page(), emptySet());
        assertEquals(0, userStocksAfterReset.getTotal());
        assertEquals(0, userStocksAfterReset.getResults().size());
    }

    @Test
    public void testAdd() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));

        Optional<User> fetched = userDB.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getId(), fetched.get().getId());
        assertEquals(user.getEmail(), fetched.get().getEmail());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        assertEquals(user.getProfileImage(), fetched.get().getProfileImage());

        Optional<UserCredits> userCredits = userCreditsDB.get(user.getId());
        assertTrue(userCredits.isPresent());
        assertEquals(10000, userCredits.get().getCredits());
    }

    @Test(expected = Exception.class)
    public void testAddUsernameConflict() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));
        user.setId(generateId("different"));
        user.setEmail("different");
        userDB.add(user); // fails on the unique username constraint
    }

    @Test
    public void testAddEmailConflictNoChange() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));
        assertEquals(0, userDB.add(user));

        Optional<User> fetched = userDB.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getId(), fetched.get().getId());
        assertEquals(user.getEmail(), fetched.get().getEmail());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        assertEquals(user.getProfileImage(), fetched.get().getProfileImage());
    }

    @Test
    public void testAddEmailConflictUpdate() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));
        user.setUsername("different");
        user.setDisplayName("different");
        user.setProfileImage("different");
        assertEquals(1, userDB.add(user));

        Optional<User> fetched = userDB.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getId(), fetched.get().getId());
        assertEquals(user.getEmail(), fetched.get().getEmail());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        assertEquals(user.getProfileImage(), fetched.get().getProfileImage());
    }

    @Test
    public void testAddEmailConflictUpdateLinkToNull() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));
        user.setProfileImage(null);
        assertEquals(1, userDB.add(user));

        Optional<User> fetched = userDB.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getId(), fetched.get().getId());
        assertEquals(user.getEmail(), fetched.get().getEmail());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        assertNull(fetched.get().getProfileImage());
    }

    @Test
    public void testAddEmailConflictUpdateNullLink() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userDB.add(user));
        user.setProfileImage("link");
        assertEquals(1, userDB.add(user));

        Optional<User> fetched = userDB.get(user.getId());
        assertTrue(fetched.isPresent());
        assertEquals(user.getId(), fetched.get().getId());
        assertEquals(user.getEmail(), fetched.get().getEmail());
        assertEquals(user.getUsername(), fetched.get().getUsername());
        assertEquals(user.getDisplayName(), fetched.get().getDisplayName());
        assertEquals(user.getProfileImage(), fetched.get().getProfileImage());
    }

    @Test
    public void testAddEmailConflictDifferentCase() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));
        user.setEmail("USER@DOMAIN.COM");
        assertEquals(0, userDB.add(user));
    }

    @Test
    public void testAddEmailConflictDifferentCaseUpdate() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));
        user.setEmail("USER@DOMAIN.COM");
        user.setDisplayName("different");
        assertEquals(1, userDB.add(user));
    }

    @Test
    public void testUpdateMissing() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(0, userDB.update(user));
    }

    @Test
    public void testUpdate() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));

        user.setUsername("updated");
        user.setDisplayName("updated");
        user.setProfileImage("updated");
        assertEquals(1, userDB.update(user));

        Optional<User> updated = userDB.get(user.getId());
        assertTrue(updated.isPresent());
        assertEquals(user.getId(), updated.get().getId());
        assertEquals(user.getEmail(), updated.get().getEmail());
        assertEquals(user.getUsername(), updated.get().getUsername());
        assertEquals(user.getDisplayName(), updated.get().getDisplayName());
        assertEquals(user.getProfileImage(), updated.get().getProfileImage());
    }

    @Test
    public void testUpdateLinkToNull() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));

        user.setProfileImage(null);
        assertEquals(1, userDB.update(user));

        Optional<User> updated = userDB.get(user.getId());
        assertTrue(updated.isPresent());
        assertEquals(user.getId(), updated.get().getId());
        assertEquals(user.getEmail(), updated.get().getEmail());
        assertEquals(user.getUsername(), updated.get().getUsername());
        assertEquals(user.getDisplayName(), updated.get().getDisplayName());
        assertNull(updated.get().getProfileImage());
    }

    @Test
    public void testUpdateNullLink() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name");
        assertEquals(1, userDB.add(user));

        user.setProfileImage("link");
        assertEquals(1, userDB.update(user));

        Optional<User> updated = userDB.get(user.getId());
        assertTrue(updated.isPresent());
        assertEquals(user.getId(), updated.get().getId());
        assertEquals(user.getEmail(), updated.get().getEmail());
        assertEquals(user.getUsername(), updated.get().getUsername());
        assertEquals(user.getDisplayName(), updated.get().getDisplayName());
        assertEquals(user.getProfileImage(), updated.get().getProfileImage());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userDB.delete("missing"));
    }

    @Test
    public void testDelete() {
        User user = new User().setId(generateId("user@domain.com")).setEmail("user@domain.com").setUsername("name").setDisplayName("Name").setProfileImage("link");
        assertEquals(1, userDB.add(user));
        assertEquals(1, userDB.delete(user.getId()));
        assertFalse(userDB.get(user.getId()).isPresent());
    }
     */
}
