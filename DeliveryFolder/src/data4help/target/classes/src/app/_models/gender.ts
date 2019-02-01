export enum Gender {
    MALE,
    FEMALE
}

export namespace Gender {

    export function values() {
        return Object.keys(Gender).filter(
            (type) => isNaN(<any>type) && type !== 'values'
        );
    }
}
