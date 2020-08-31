
export class Achievement {
    id: string;
    name: string;
    category: string;
    description: string;
    order: number;
}


export class ActivityLog {
    id: string;
    userId: string;
    type: string;
    timestamp: string;
    market: string;
    symbol: string;
    shares: number;
    price: number;
}


export class Delta {
    interval: string;
    change: number;
    percent: number;
}


export class ErrorResponse {
    status: number;
    message: string;
}


export class Page {
    page: number;
    size: number;
    totalPages: number;
    firstRow: number;
    lastRow: number;
    totalRows: number;
    previous: Page;
    next: Page;
}


export class PricedStock {
    market: string;
    symbol: string;
    name: string;
    profileImage: string;
    timestamp: string;
    price: number;
}


export class PricedUserStock {
    userId: string;
    market: string;
    symbol: string;
    name: string;
    profileImage: string;
    timestamp: string;
    shares: number;
    price: number;
}


export class Results<T> {
    total: number;
    page: Page;
    results: T[];
}


export enum DatabaseField {
    ACHIEVEMENT_ID,
    BATCH,
    CHANGE,
    COUNT,
    CREDITS,
    DESCRIPTION,
    DIFFICULTY,
    DISPLAY_NAME,
    EMAIL,
    ID,
    MARKET,
    NAME,
    PERCENT,
    PRICE,
    RANK,
    SHARES,
    SYMBOL,
    TIMESTAMP,
    TOTAL,
    TYPE,
    USERNAME,
    USER_ID,
    USERS,
    VALUE
}


export enum SortDirection {
    ASC,
    DESC
}

export class Sort {
    field: DatabaseField;
    direction: SortDirection;
}


export class Stock {
    market: string;
    symbol: string;
    name: string;
    profileImage: string;
}


export class StockActivityLog {
    id: string;
    userId: string;
    type: string;
    timestamp: string;
    market: string;
    symbol: string;
    name: string;
    profileImage: string;
    shares: number;
    price: number;
}


export class StockPrice {
    market: string;
    symbol: string;
    timestamp: string;
    price: number;
}


export class StockPriceChange {
    batch: number;
    market: string;
    symbol: string;
    timestamp: string;
    price: number;
    change: number;
    percent: number;
}


export class StockPriceChangeCollection {
    changes: StockPriceChange[];
}


export class User {
    id: string;
    username: string;
    displayName: string;
}


export class UserAchievement {
    userId: string;
    achievementId: string;
    timestamp: string;
    description: string;
}


export class UserCredits {
    userId: string;
    credits: number;
}


export class UsernameCheck {
    username: string;
    exists: boolean;
    valid: boolean;
    message: string;
}


export class UserReset {
    user: User;
    reset: boolean;
}


export class UserStock {
    userId: string;
    market: string;
    symbol: string;
    shares: number;
}


// portfolio

export class CreditRank {
    batch: number;
    userId: string;
    timestamp: string;
    rank: number;
    value: number;
}


export class CreditRankCollection {
    ranks: CreditRank[];
    deltas: Delta[];
}


export class MarketRank {
    batch: number;
    userId: string;
    market: string;
    timestamp: string;
    rank: number;
    value: number;
}


export class MarketRankCollection {
    market: string;
    ranks: MarketRank[];
    deltas: Delta[];
}


export class MarketTotalRank {
    batch: number;
    userId: string;
    timestamp: string;
    rank: number;
    value: number;
}


export class MarketTotalRankCollection {
    ranks: MarketTotalRank[];
    deltas: Delta[];
}


export class TotalRank {
    batch: number;
    userId: string;
    timestamp: string;
    rank: number;
    value: number;
}


export class TotalRankCollection {
    ranks: TotalRank[];
    deltas: Delta[];
}


export class RankedUser {
    user: User[];
    batch: number;
    timestamp: string;
    rank: number;
    value: number;
}


export class MarketValue {
    market: string;
    value: number;
}


export class PortfolioValueSummary {
    userId: string;
    credits: number;
    marketTotal: number;
    marketValues: MarketValue[];
    total: number;
}


export class PortfolioValue {
    summary: PortfolioValueSummary;
    creditRanks: CreditRankCollection;
    marketTotalRanks: MarketTotalRankCollection;
    marketRanks: MarketRankCollection[];
    totalRanks: TotalRankCollection;
}


// system


export class ActiveTransactionCount {
    timestamp: string;
    count: number;
}


export class ActiveTransactionCountCollection {
    counts: ActiveTransactionCount[];
    deltas: Delta[];
}


export class ActiveUserCount {
    timestamp: string;
    count: number;
}


export class ActiveUserCountCollection {
    counts: ActiveUserCount[];
    deltas: Delta[];
}


export class TotalTransactionCount {
    timestamp: string;
    count: number;
}


export class TotalTransactionCountCollection {
    counts: TotalTransactionCount[];
    deltas: Delta[];
}


export class TotalUserCount {
    timestamp: string;
    count: number;
}


export class TotalUserCountCollection {
    counts: TotalUserCount[];
    deltas: Delta[];
}


export class OverallCreditValue {
    timestamp: string;
    value: number;
}


export class OverallCreditValueCollection {
    values: OverallCreditValue[];
    deltas: Delta[];
}


export class OverallMarketTotalValue {
    timestamp: string;
    value: number;
}


export class OverallMarketTotalValueCollection {
    values: OverallMarketTotalValue[];
    deltas: Delta[];
}


export class OverallMarketValue {
    market: string;
    timestamp: string;
    value: number;
}


export class OverallMarketValueCollection {
    market: string;
    values: OverallMarketValue[];
    deltas: Delta[];
}


export class OverallTotalValue {
    timestamp: string;
    value: number;
}


export class OverallTotalValueCollection {
    values: OverallMarketValue[];
    deltas: Delta[];
}

