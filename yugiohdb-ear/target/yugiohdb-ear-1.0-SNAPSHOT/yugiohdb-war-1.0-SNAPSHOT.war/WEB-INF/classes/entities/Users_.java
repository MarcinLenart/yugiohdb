package entities;

import java.util.List;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-07-24T19:57:16")
@StaticMetamodel(Users.class)
public class Users_ { 

    public static volatile SingularAttribute<Users, Long> id;
    public static volatile SingularAttribute<Users, String> login;
    public static volatile SingularAttribute<Users, Boolean> logged;
    public static volatile SingularAttribute<Users, String> password;
    public static volatile SingularAttribute<Users, List> ownedCards;

}