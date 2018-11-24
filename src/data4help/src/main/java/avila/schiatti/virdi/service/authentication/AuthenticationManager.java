package avila.schiatti.virdi.service.authentication;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.ThirdParty;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class AuthenticationManager {

    private final static String SECRET_PREFIX = "lA7eBr1c0le";
    private final static Integer DEFAULT_TTL_SECONDS = 3600;
    private static AuthenticationManager _instance = null;

    private RedisCommands<String, String> commands;

    private AuthenticationManager(){
        StaticConfiguration configuration = StaticConfiguration.getInstance();

        RedisClient redisClient = RedisClient.create(configuration.getRedisConnectionString());
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        this.commands = connection.sync();
    }

    @NotNull
    public static AuthenticationManager getInstance(){
        if(_instance == null){
            _instance = new AuthenticationManager();
        }
        return _instance;
    }

    @NotNull
    private String createAccessToken(String id){
        String stringToEncrypt = SECRET_PREFIX.concat(id).concat((new Date()).toString());
        return new String(DigestUtils.sha512(stringToEncrypt));
    }

    public void validateAndUpdateAccessToken(String userId, String accessToken) throws TrackMeException {
        if(accessToken == null){
            throw new TrackMeException(TrackMeError.NULL_TOKEN);
        }

        if(userId == null){
            throw new TrackMeException(TrackMeError.NOT_VALID_USER);
        }

        String storedUserId = commands.get(accessToken);

        // the token does not exist in the Redis DB
        if (storedUserId == null){
            throw new TrackMeException(TrackMeError.NOT_VALID_TOKEN);
        }

        // the token belongs to a different user
        if(userId.equals(storedUserId) == Boolean.FALSE){
            throw new TrackMeException(TrackMeError.NOT_VALID_SESSION);
        }

        this.updateAccessToken(accessToken);
    }

    private void updateAccessToken(@NotNull String accessToken) {
        // set new expiration + 1 hour
        commands.expire(accessToken, DEFAULT_TTL_SECONDS);
    }

    public void validateSecretKey(String appId, String secretKey) throws TrackMeException {
        if(secretKey == null){
            throw new TrackMeException(TrackMeError.NOT_VALID_SECRET_KEY);
        }

        // we save the token as a key and the thirdPartyId as value, if we find the token => thirdPartyId, the token is valid
        String thirdPartyAppId = commands.get(secretKey);

        if (thirdPartyAppId == null || thirdPartyAppId.equals(appId) == Boolean.FALSE){
            throw new TrackMeException(TrackMeError.NOT_VALID_SECRET_KEY);
        }
    }

    public UserWebAuth setUserAccessToken(@NotNull D4HUser d4HUser){
        String userId = d4HUser.getId().toString();
        String accessToken = this.createAccessToken(userId);

        SetArgs args = SetArgs.Builder.ex(DEFAULT_TTL_SECONDS);
        commands.set(accessToken, userId, args);

        return new UserWebAuth(userId, accessToken);
    }

    public ThirdPartyApiAuth setThirdPartySecretKey(@NotNull ThirdParty thirdParty){
        String tpId = thirdParty.getId().toString();
        String appId = DigestUtils.md5Hex(tpId);

        String secretKey = this.createAccessToken(tpId);

        // third parties secret key has not TTL
        commands.set(secretKey, appId);

        return new ThirdPartyApiAuth(secretKey, appId);
    }

    public void deleteAccessToken(String accessToken) {
        commands.del(accessToken);
        throw new TrackMeException(TrackMeError.NOT_VALID_USER);
    }

    public String hashPassword(String password){
        return DigestUtils.sha512Hex(password);
    }
}
