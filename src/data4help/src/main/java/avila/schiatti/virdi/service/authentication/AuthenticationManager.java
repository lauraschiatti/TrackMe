package avila.schiatti.virdi.service.authentication;

import avila.schiatti.virdi.configuration.StaticConfiguration;
import avila.schiatti.virdi.exception.TrackMeError;
import avila.schiatti.virdi.exception.TrackMeException;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.utils.Validator;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

public class AuthenticationManager {

    private final static String SECRET_PREFIX = "lA7eBr1c0le";
    public final static Long DEFAULT_TTL_SECONDS = 3600L;
    private static AuthenticationManager _instance = null;

    private RedisCommands<String, String> commands;

    private AuthenticationManager(){
        StaticConfiguration configuration = StaticConfiguration.getInstance();

        RedisClient redisClient = RedisClient.create(configuration.getRedisConnectionString());
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        this.commands = connection.sync();
    }

    private AuthenticationManager(RedisCommands<String, String> commands){
        this.commands = commands;
    }

    public static AuthenticationManager getInstance(){
        if(_instance == null){
            _instance = new AuthenticationManager();
        }
        return _instance;
    }

    /**
     * Only for test method
     * @param commands RedisCommands<String, String>
     */
    public static void createForTestingOnly(RedisCommands<String, String> commands){
        _instance = new AuthenticationManager(commands);
    }


    public String createToken(String id){
        String stringToEncrypt = SECRET_PREFIX.concat(id).concat((new Date()).toString());
        return DigestUtils.sha512Hex(stringToEncrypt);
    }

    public void validateAndUpdateAccessToken(String userId, String accessToken) {
        // validate access token
        this.validateAccessToken(userId, accessToken);
        // update access token TTL
        this.updateAccessToken(accessToken);
    }

    public void validateAccessToken(String userId, String accessToken) {
        if(Validator.isNullOrEmpty(accessToken)){
            throw new TrackMeException(TrackMeError.NULL_TOKEN);
        }

        if(Validator.isNullOrEmpty(userId)){
            throw new TrackMeException(TrackMeError.NOT_VALID_USER);
        }

        String storedUserId = commands.get(accessToken);

        // the token does not exist in the Redis DB
        if (Validator.isNullOrEmpty(storedUserId)){
            throw new TrackMeException(TrackMeError.NOT_VALID_SESSION);
        }

        // the token belongs to a different user
        if(userId.equals(storedUserId) == Boolean.FALSE){
            throw new TrackMeException(TrackMeError.NOT_VALID_USER);
        }
    }

    private void updateAccessToken(String accessToken) {
        // set new expiration + 1 hour
        commands.expire(accessToken, DEFAULT_TTL_SECONDS);
    }

    public void validateSecretKey(String appId, String secretKey) throws TrackMeException {
        if(Validator.isNullOrEmpty(secretKey) || Validator.isNullOrEmpty(appId)){
            throw new TrackMeException(TrackMeError.NOT_VALID_SECRET_KEY);
        }

        // we save the token as a key and the thirdPartyId as value, if we find the token => thirdPartyId, the token is valid
        String thirdPartyAppId = commands.get(secretKey);

        if (Validator.isNullOrEmpty(thirdPartyAppId) || appId.equals(thirdPartyAppId) == Boolean.FALSE){
            throw new TrackMeException(TrackMeError.NOT_VALID_SECRET_KEY);
        }
    }

    public UserWebAuth setUserAccessToken(D4HUser d4HUser){
        String userId = d4HUser.getId().toString();
        String accessToken = this.createToken(userId);

        SetArgs args = SetArgs.Builder.ex(DEFAULT_TTL_SECONDS);
        commands.set(accessToken, userId, args);

        return new UserWebAuth(userId, accessToken);
    }

    /**
     * Creates a secret key and an app_id, used by third parties calls, using a String as a seed
     * @param seed Seed string used to generate the APP_ID and SECRET_KEY
     * @return ThirdPartyApiAuth object
     */
    public ThirdPartyApiAuth setThirdPartySecretKey(String seed){
        String appId = DigestUtils.md5Hex(seed);

        String secretKey = this.createToken(seed);

        // third parties secret key has not TTL
        commands.set(secretKey, appId);

        return new ThirdPartyApiAuth(secretKey, appId);
    }

    public void deleteAccessToken(String accessToken) {
        commands.del(accessToken);
    }

    public static String hashPassword(String password){
        return DigestUtils.sha512Hex(password);
    }
}
