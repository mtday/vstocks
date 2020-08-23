package vstocks.db;

public class UserStockServiceImplIT extends BaseServiceImplIT {
    /*
    private ActivityLogTable activityLogTable;
    private UserDB userTable;
    private StockDB stockTable;
    private StockPriceDB stockPriceTable;
    private UserCreditsDB userCreditsTable;
    private UserStockDB userStockTable;
    private UserStockServiceImpl userStockDB;

    private final User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
    private final UserCredits userCredits1 = new UserCredits().setUserId(user1.getId()).setCredits(10);
    private final UserCredits userCredits2 = new UserCredits().setUserId(user2.getId()).setCredits(10);
    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
    private final StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setPrice(10);
    private final StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(Instant.now()).setPrice(20);

    @Before
    public void setup() throws SQLException {
        activityLogTable = new ActivityLogTable();
        userTable = new UserDB();
        stockTable = new StockDB();
        stockPriceTable = new StockPriceDB();
        userCreditsTable = new UserCreditsDB();
        userStockTable = new UserStockDB();
        userStockDB = new UserStockServiceImpl(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            assertEquals(1, userCreditsTable.add(connection, userCredits1));
            assertEquals(1, userCreditsTable.add(connection, userCredits2));
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userStockTable.truncate(connection);
            stockPriceTable.truncate(connection);
            stockTable.truncate(connection);
            userCreditsTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(userStockDB.get("missing-id", TWITTER, "missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock));

        Optional<UserStock> fetched = userStockDB.get(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(userStock.getUserId(), fetched.get().getUserId());
        assertEquals(userStock.getMarket(), fetched.get().getMarket());
        assertEquals(userStock.getSymbol(), fetched.get().getSymbol());
        assertEquals(userStock.getShares(), fetched.get().getShares());
    }

    @Test
    public void testGetForUserNone() {
        Results<UserStock> results = userStockDB.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));

        Results<UserStock> results = userStockDB.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock1, results.getResults().get(0));
        assertEquals(userStock2, results.getResults().get(1));
    }

    @Test
    public void testGetForUserSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), USER_ID.toSort()));
        Results<UserStock> results = userStockDB.getForUser(user1.getId(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock2, results.getResults().get(0));
        assertEquals(userStock1, results.getResults().get(1));
    }

    @Test
    public void testGetForStockNone() {
        Results<UserStock> results = userStockDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));

        Results<UserStock> results = userStockDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock1, results.getResults().get(0));
        assertEquals(userStock2, results.getResults().get(1));
    }

    @Test
    public void testGetForStockSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        Results<UserStock> results = userStockDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock2, results.getResults().get(0));
        assertEquals(userStock1, results.getResults().get(1));
    }

    @Test
    public void testGetAllNone() {
        Results<UserStock> results = userStockDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));

        Results<UserStock> results = userStockDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock1, results.getResults().get(0));
        assertEquals(userStock2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        Results<UserStock> results = userStockDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock2, results.getResults().get(0));
        assertEquals(userStock1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<UserStock> list = new ArrayList<>();
        assertEquals(0, userStockDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));

        List<UserStock> list = new ArrayList<>();
        assertEquals(2, userStockDB.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(userStock1, list.get(0));
        assertEquals(userStock2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));

        List<UserStock> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        assertEquals(2, userStockDB.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(userStock2, list.get(0));
        assertEquals(userStock1, list.get(1));
    }

    @Test
    public void testBuyStockZeroShares() {
        assertEquals(0, userStockDB.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 0));
    }

    @Test
    public void testBuyStockNegativeShares() {
        assertEquals(0, userStockDB.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), -1));
    }

    @Test
    public void testBuyStockNoExistingUserStock() throws SQLException {
        assertEquals(1, userStockDB.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user credits were updated
            Optional<UserCredits> userCredits = userCreditsTable.get(connection, user1.getId());
            assertTrue(userCredits.isPresent());
            assertEquals(userCredits1.getCredits() - stockPrice1.getPrice(), userCredits.get().getCredits());

            // Make sure user stock was created/updated
            Optional<UserStock> userStock = userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(userStock.isPresent());
            assertEquals(1, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getSymbol());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(STOCK_BUY, activityLog.getType());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(TWITTER, activityLog.getMarket());
            assertEquals(stock1.getSymbol(), activityLog.getSymbol());
            assertEquals(stockPrice1.getPrice(), (long) activityLog.getPrice());
            assertEquals(1, (int) activityLog.getShares());
        }
    }

    @Test
    public void testBuyStockWithExistingUserStock() throws SQLException {
        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1);
        userStockDB.add(existingUserStock);

        assertEquals(1, userStockDB.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user credits were updated
            Optional<UserCredits> userCredits = userCreditsTable.get(connection, user1.getId());
            assertTrue(userCredits.isPresent());
            assertEquals(userCredits1.getCredits() - stockPrice1.getPrice(), userCredits.get().getCredits());

            // Make sure user stock was created/updated
            Optional<UserStock> userStock = userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(userStock.isPresent());
            assertEquals(2, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getSymbol());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(STOCK_BUY, activityLog.getType());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(TWITTER, activityLog.getMarket());
            assertEquals(stock1.getSymbol(), activityLog.getSymbol());
            assertEquals(stockPrice1.getPrice(), (long) activityLog.getPrice());
            assertEquals(1, (int) activityLog.getShares());
        }
    }

    @Test
    public void testBuyStockCreditsTooLow() throws SQLException {
        assertEquals(0, userStockDB.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 2));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user credits were NOT updated
            Optional<UserCredits> userCredits = userCreditsTable.get(connection, user1.getId());
            assertTrue(userCredits.isPresent());
            assertEquals(userCredits1.getCredits(), userCredits.get().getCredits());

            // Make sure no user stock was created/updated
            assertFalse(userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testBuyStockMissingStockPrice() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockPriceTable.truncate(connection);
            connection.commit();
        }

        assertEquals(0, userStockDB.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user credits were NOT updated
            Optional<UserCredits> userCredits = userCreditsTable.get(connection, user1.getId());
            assertTrue(userCredits.isPresent());
            assertEquals(userCredits1.getCredits(), userCredits.get().getCredits());

            // Make sure no user stock was created/updated
            assertFalse(userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testSellStockZeroShares() {
        assertEquals(0, userStockDB.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 0));
    }

    @Test
    public void testSellStockNegativeShares() {
        assertEquals(0, userStockDB.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), -1));
    }

    @Test
    public void testSellStockNoExistingUserStock() throws SQLException {
        assertEquals(0, userStockDB.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user credits were NOT updated
            Optional<UserCredits> userCredits = userCreditsTable.get(connection, user1.getId());
            assertTrue(userCredits.isPresent());
            assertEquals(userCredits1.getCredits(), userCredits.get().getCredits());

            // Make sure no user stock was created/updated
            assertFalse(userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testSellStockWithExistingUserStock() throws SQLException {
        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        userStockDB.add(existingUserStock);

        assertEquals(1, userStockDB.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user credits were updated
            Optional<UserCredits> userCredits = userCreditsTable.get(connection, user1.getId());
            assertTrue(userCredits.isPresent());
            assertEquals(userCredits1.getCredits() + stockPrice1.getPrice(), userCredits.get().getCredits());

            // Make sure user stock was updated
            Optional<UserStock> userStock = userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(userStock.isPresent());
            assertEquals(9, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getSymbol());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(STOCK_SELL, activityLog.getType());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(TWITTER, activityLog.getMarket());
            assertEquals(stock1.getSymbol(), activityLog.getSymbol());
            assertEquals(stockPrice1.getPrice(), (long) activityLog.getPrice());
            assertEquals(-1, (int) activityLog.getShares());
        }
    }

    @Test
    public void testSellStockWithExistingUserStockDownToZero() throws SQLException {
        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1);
        userStockDB.add(existingUserStock);

        assertEquals(1, userStockDB.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user credits were updated
            Optional<UserCredits> userCredits = userCreditsTable.get(connection, user1.getId());
            assertTrue(userCredits.isPresent());
            assertEquals(userCredits1.getCredits() + stockPrice1.getPrice(), userCredits.get().getCredits());

            // Make sure user stock was deleted, since the shares dropped to 0
            assertFalse(userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getSymbol());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(STOCK_SELL, activityLog.getType());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(TWITTER, activityLog.getMarket());
            assertEquals(stock1.getSymbol(), activityLog.getSymbol());
            assertEquals(stockPrice1.getPrice(), (long) activityLog.getPrice());
            assertEquals(-1, (int) activityLog.getShares());
        }
    }

    @Test
    public void testSellStockMissingStockPrice() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockPriceTable.truncate(connection);
            connection.commit();
        }

        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(5);
        userStockDB.add(existingUserStock);

        assertEquals(0, userStockDB.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user credits were NOT updated
            Optional<UserCredits> userCredits = userCreditsTable.get(connection, user1.getId());
            assertTrue(userCredits.isPresent());
            assertEquals(userCredits1.getCredits(), userCredits.get().getCredits());

            // Make sure the user stock was not updated
            Optional<UserStock> userStock = userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(userStock.isPresent());
            assertEquals(existingUserStock.getShares(), userStock.get().getShares());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testAdd() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock));
        userStockDB.add(userStock);
    }

    @Test
    public void testUpdatePositiveMissing() {
        assertEquals(1, userStockDB.update(user1.getId(), TWITTER, stock1.getSymbol(), 10));

        Optional<UserStock> fetched = userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(10, fetched.get().getShares());
    }

    @Test
    public void testUpdatePositiveExisting() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock));
        assertEquals(1, userStockDB.update(user1.getId(), TWITTER, stock1.getSymbol(), 10));

        Optional<UserStock> fetched = userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(20, fetched.get().getShares());
    }

    @Test
    public void testUpdateNegativeMissing() {
        assertEquals(0, userStockDB.update(user1.getId(), TWITTER, stock1.getSymbol(), -10));
        // Nothing was added
        assertFalse(userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());
    }

    @Test
    public void testUpdateNegativeExistingValid() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock));
        assertEquals(1, userStockDB.update(user1.getId(), TWITTER, stock1.getSymbol(), -5));

        Optional<UserStock> fetched = userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(5, fetched.get().getShares());
    }

    @Test
    public void testUpdateNegativeValidToZero() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock));
        assertEquals(1, userStockDB.update(user1.getId(), TWITTER, stock1.getSymbol(), -10));
        assertFalse(userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());
    }

    @Test
    public void testUpdateNegativeExistingInvalid() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock));
        assertEquals(0, userStockDB.update(user1.getId(), TWITTER, stock1.getSymbol(), -15));

        Optional<UserStock> fetched = userStockDB.get(user1.getId(), TWITTER, stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(10, fetched.get().getShares()); // Not updated
    }

    @Test
    public void testUpdateZero() {
        assertEquals(0, userStockDB.update(user1.getId(), TWITTER, stock1.getSymbol(), 0));
    }

    @Test
    public void testDeleteForUserMissing() {
        assertEquals(0, userStockDB.deleteForUser("missing-id"));
    }

    @Test
    public void testDeleteForUser() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock3 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock4 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);

        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));
        assertEquals(1, userStockDB.add(userStock3));
        assertEquals(1, userStockDB.add(userStock4));

        assertEquals(2, userStockDB.deleteForUser(user1.getId()));

        Results<UserStock> results = userStockDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock3, results.getResults().get(0));
        assertEquals(userStock4, results.getResults().get(1));
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userStockDB.delete("missing-id", TWITTER, "missing-id"));
    }

    @Test
    public void testDelete() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock));
        assertEquals(1, userStockDB.delete(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol()));
        assertFalse(userStockDB.get(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol()).isPresent());
    }
     */
}
