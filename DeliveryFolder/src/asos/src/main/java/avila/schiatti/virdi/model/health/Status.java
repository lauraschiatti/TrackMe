package avila.schiatti.virdi.model.health;

import xyz.morphia.annotations.Embedded;

@Embedded
public enum Status {
    STABLE, CRITICAL
}
