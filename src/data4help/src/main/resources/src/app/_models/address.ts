export enum Address {
    A_POSITIVE,
    A_NEGATIVE,
    B_POSITIVE,
    B_NEGATIVE,
    AB_POSITIVE,
    AB_NEGATIVE,
    ZERO_POSITIVE,
    ZERO_NEGATIVE
}

export namespace Address {

    export function values() {
        return Object.keys(Address).filter(
            (type) => isNaN(<any>type) && type !== 'values'
        );
    }
}
