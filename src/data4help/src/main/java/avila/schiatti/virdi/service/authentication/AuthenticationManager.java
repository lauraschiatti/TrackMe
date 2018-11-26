package avila.schiatti.virdi.service.authentication;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.utils.Validator;
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
        return DigestUtils.sha512Hex(stringToEncrypt);
    }

    public void validateAndUpdateAccessToken(String userId, String accessToken) throws TrackMeException {
        // validate access token
        this.validateAccessToken(userId, accessToken);
        // update access token TTL
        this.updateAccessToken(accessToken);
    }

    public void validateAccessToken(String userId, String accessToken) throws TrackMeException {
        if(Validator.isNullOrEmpty(accessToken)){
            throw new TrackMeException(TrackMeError.NULL_TOKEN);
        }

        if(Validator.isNullOrEmpty(userId)){
            throw new TrackMeException(TrackMeError.NOT_VALID_USER);
        }

        String storedUserId = commands.get(accessToken);

        // the token does not exist in the Redis DB
        if (Validator.isNullOrEmpty(storedUserId)){
            throw new TrackMeException(TrackMeError.NOT_VALID_TOKEN);
        }

        // the token belongs to a different user
        if(userId.equals(storedUserId) == Boolean.FALSE){
            throw new TrackMeException(TrackMeError.NOT_VALID_SESSION);
        }
    }

    private void updateAccessToken(@NotNull String accessToken) {
        // set new expiration + 1 hour
        commands.expire(accessToken, DEFAULT_TTL_SECONDS);
    }

    public void validateSecretKey(String appId, String secretKey) throws TrackMeException {
        if(Validator.isNullOrEmpty(secretKey)){
            throw new TrackMeException(TrackMeError.NOT_VALID_SECRET_KEY);
        }

        // we save the token as a key and the thirdPartyId as value, if we find the token => thirdPartyId, the token is valid
        String thirdPartyAppId = commands.get(secretKey);

        if (Validator.isNullOrEmpty(thirdPartyAppId) || thirdPartyAppId.equals(appId) == Boolean.FALSE){
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

    /**
     * Creates a secret key and an app_id, used by third parties calls, using a String as a seed
     * @param seed Seed string used to generate the APP_ID and SECRET_KEY
     * @return ThirdPartyApiAuth object
     */
    public ThirdPartyApiAuth setThirdPartySecretKey(@NotNull String seed){
        String appId = DigestUtils.md5Hex(seed);

        String secretKey = this.createAccessToken(seed);

        // third parties secret key has not TTL
        commands.set(secretKey, appId);

        return new ThirdPartyApiAuth(secretKey, appId);
    }

    public void deleteAccessToken(String accessToken) {
        commands.del(accessToken);
    }

    public String hashPassword(String password){
        return DigestUtils.sha512Hex(password);
    }
}
