package avila.schiatti.virdi.model.data;

import xyz.morphia.annotations.Embedded;

@Embedded
public enum BloodType {
    A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE, AB_NEGATIVE, ZERO_POSITIVE, ZERO_NEGATIVE
}
