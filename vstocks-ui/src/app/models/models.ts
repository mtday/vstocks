
export class Delta {
    interval: string;
    change: number;
    percent: number;
}


export class Page {
    page: number;
    size: number;
}


export class User {
    id: string;
    username: string;
    displayName: string;
}


export class UserCredits {
    userId: string;
    credits: number;
}


export class UserReset {
    user: User;
    reset: boolean;
}


export class UsernameCheck {
    username: string;
    exists: boolean;
    valid: boolean;
    message: string;
}


export class CreditRank {
    batch: number;
    userId: string;
    timestamp: string;
    rank: number;
    value: number;
}


export class CreditRankCollection {
    ranks: CreditRank[];
    deltas: Map<string, Delta>;
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
    ranks: MarketRank[];
    deltas: Map<string, Delta>;
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
    deltas: Map<string, Delta>;
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
    deltas: Map<string, Delta>;
}
