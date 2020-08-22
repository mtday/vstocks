package vstocks.achievement.impl.beginner;

import vstocks.achievement.AchievementFinder;
import vstocks.achievement.AchievementProvider;
import vstocks.db.ServiceFactory;
import vstocks.db.PreparedStatementCreator;
import vstocks.db.RowMapper;
import vstocks.model.Achievement;
import vstocks.model.ActivityType;
import vstocks.model.Market;
import vstocks.model.UserAchievement;

import java.sql.PreparedStatement;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.toList;
import static vstocks.model.AchievementCategory.BEGINNER;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.ActivityType.STOCK_SELL;

public class FirstStockActivity implements AchievementProvider {
    static String getId(Market market, ActivityType activityType) {
        return "first_" + activityType.name().toLowerCase(ENGLISH) + "_" + market.name().toLowerCase(ENGLISH);
    }

    @Override
    public List<Entry<Achievement, AchievementFinder>> getAchievements() {
        return Arrays.stream(Market.values()).flatMap(market -> Stream.of(
                new FirstStockActivityFinder(getId(market, STOCK_BUY), market, STOCK_BUY),
                new FirstStockActivityFinder(getId(market, STOCK_SELL), market, STOCK_SELL)
        )).map(v -> new AbstractMap.SimpleEntry<>(v.getAchievement(), (AchievementFinder) v)).collect(toList());
    }

    static class FirstStockActivityFinder implements AchievementFinder {
        private final String achievementId;
        private final Market market;
        private final ActivityType activityType;

        public FirstStockActivityFinder(String achievementId, Market market, ActivityType activityType) {
            this.achievementId = achievementId;
            this.market = market;
            this.activityType = activityType;
        }

        public Achievement getAchievement() {
            String marketName = market.getDisplayName();
            String nameVerb = activityType == STOCK_BUY ? "Purchase" : "Sale";
            String descriptionVerb = activityType == STOCK_BUY ? "Buy (with credits)" : "Sell";
            return new Achievement()
                    .setId(achievementId)
                    .setName(format("First %s Stock %s", marketName, nameVerb))
                    .setCategory(BEGINNER)
                    .setOrder(activityType == STOCK_BUY ? 5 : 10) // put purchase achievements before sale achievements
                    .setDescription(format("%s one or more shares of any stock on the %s market for any price.",
                            descriptionVerb, marketName));
        }

        @Override
        public int find(ServiceFactory dbFactory, Set<String> userIds, Consumer<UserAchievement> consumer) {
            PreparedStatementCreator psc = conn -> {
                String sql = "WITH data AS ("
                        + "  SELECT user_id, COUNT(user_id) AS count "
                        + "  FROM activity_logs "
                        + "  WHERE user_id = ANY(?) "
                        + "    AND user_id NOT IN (SELECT user_id FROM user_achievements WHERE achievement_id = ?) "
                        + "    AND market = ? AND type = ? "
                        + "  GROUP BY user_id "
                        + ") "
                        + "SELECT DISTINCT ON (a.user_id) a.user_id, a.timestamp, a.symbol, a.shares, a.price "
                        + "FROM activity_logs a "
                        + "JOIN data ON (data.user_id = a.user_id) "
                        + "WHERE data.count = 1 "
                        + "ORDER BY a.user_id, timestamp DESC";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setArray(1, conn.createArrayOf("varchar", userIds.toArray()));
                ps.setString(2, achievementId);
                ps.setString(3, market.name());
                ps.setString(4, activityType.name());
                return ps;
            };
            RowMapper<UserAchievement> rowMapper = rs -> {
                String presentTenseVerb = activityType == STOCK_BUY ? "purchase" : "sale";
                String pastTenseVerb = activityType == STOCK_BUY ? "bought" : "sold";
                String description = format("First %s stock %s achieved - %d shares of %s %s for %d credits each.",
                        market.getDisplayName(), presentTenseVerb, Math.abs(rs.getInt("shares")),
                        rs.getString("symbol"), pastTenseVerb, rs.getInt("price"));
                return new UserAchievement()
                        .setUserId(rs.getString("user_id"))
                        .setAchievementId(achievementId)
                        .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                        .setDescription(description);
            };

            return dbFactory.getActivityLogDB().consume(psc, rowMapper, consumer);
        }
    }
}
