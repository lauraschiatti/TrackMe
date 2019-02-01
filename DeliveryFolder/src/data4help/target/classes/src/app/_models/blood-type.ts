export enum BloodType {
    A_POSITIVE,
    A_NEGATIVE,
    B_POSITIVE,
    B_NEGATIVE,
    AB_POSITIVE,
    AB_NEGATIVE,
    ZERO_POSITIVE,
    ZERO_NEGATIVE
}

export namespace BloodType {

    export function values() {
        return Object.keys(BloodType).filter(
            (type) => isNaN(<any>type) && type !== 'values'
        );
    }
}
