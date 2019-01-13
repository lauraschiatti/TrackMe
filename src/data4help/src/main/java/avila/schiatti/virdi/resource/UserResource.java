package avila.schiatti.virdi.resource;

import avila.schiatti.virdi.model.data.Address;
import avila.schiatti.virdi.model.subscription.D4HQuery;
import avila.schiatti.virdi.model.user.D4HUser;
import avila.schiatti.virdi.model.user.Individual;
import avila.schiatti.virdi.model.user.ThirdParty;
import avila.schiatti.virdi.utils.Validator;
import xyz.morphia.Datastore;
import xyz.morphia.annotations.IndexOptions;
import xyz.morphia.query.CriteriaContainerImpl;
import xyz.morphia.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserResource extends Resource<D4HUser> {

    /**
     * Only for testing method
     * @param datastore
     */
    public UserResource(Datastore datastore){
        super(datastore, D4HUser.class);
    }

    private UserResource(){
        super(D4HUser.class);
    }

    public static UserResource create(){
        return new UserResource();
    }

    public D4HUser getByEmailAndPass(String email, String password){
        return this.datastore
                .find(D4HUser.class)
                .field("email")
                .equal(email)
                .field("password")
                .equal(password)
                .get();
    }

    public ThirdParty getThirdPartyBySecretKey(String secretKey){
        return this.datastore.find(ThirdParty.class)
                .field("secretKey")
                .equal(secretKey)
                .get();
    }

    public Individual getBySSN(String ssn){
        return this.datastore.find(Individual.class)
                .field("ssn")
                .equal(ssn)
                .get();
    }

    public List<Individual> getByQuery(D4HQuery query) {
        Query<Individual> q = datastore.find(Individual.class);

        if(!Validator.isNullOrEmpty(query.getCountry())){
            q = q.field("address.country").equal(query.getCountry());
            if(!Validator.isNullOrEmpty(query.getProvince())){
                q = q.field("address.province").equal(query.getProvince());
                if(!Validator.isNullOrEmpty(query.getCity())){
                    q = q.field("address.city").equal(query.getCity());
                }
            }
        }

        if(query.getGender() != null){
            q = q.field("gender").equal(query.getGender());
        }

        if(query.getBloodType() != null){
            q = q.field("bloodType").equal(query.getBloodType());
        }

        if(query.getMinAge() != null && query.getMaxAge() != null && query.getMinAge() <= query.getMaxAge()){
            q = q.field("birthDate").greaterThanOrEq(LocalDate.now().minusYears(query.getMaxAge()));
            q = q.field("birthDate").lessThanOrEq(LocalDate.now().minusYears(query.getMinAge()));
        } else if(query.getMinAge() != null){
            q = q.field("birthDate").lessThanOrEq(LocalDate.now().minusYears(query.getMinAge()));
        } else if(query.getMaxAge() != null){
            q = q.field("birthDate").greaterThanOrEq(LocalDate.now().minusYears(query.getMaxAge()));
        }

        return q.asList();
    }

    public List<Individual> getAllByAddress(Address address){
        Query<Individual> q = datastore.find(Individual.class);

        if(!Validator.isNullOrEmpty(address.getCountry())){
            q = q.field("address.country").equal(address.getCountry());
        }
        if(!Validator.isNullOrEmpty(address.getProvince())){
            q = q.field("address.province").equal(address.getProvince());
        }
        if(!Validator.isNullOrEmpty(address.getCity())){
            q = q.field("address.city").equal(address.getCity());
        }

        return q.asList();
    }

    public D4HUser getByEmail(String email){
        return this.datastore
                .find(D4HUser.class)
                .field("email")
                .equal(email)
                .get();
    }
}
