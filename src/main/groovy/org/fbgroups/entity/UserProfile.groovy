package org.fbgroups.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * Created by Pavel on 1/16/2016.
 */
@Entity
@Table(name="userprofile")
class UserProfile {
    @Id
    String userid;

    String name;

    String firstname;

    String lastname;

    String email;

    String username;

    UserProfile() {
    }

    public UserProfile(String userId, String name, String firstName, String lastName, String email, String username) {
        this.userid = userid;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;

        fixName();
    }

    //{ "id", "about", "age_range", "birthday", "context", "cover", "currency", "devices", "education", "email", "favorite_athletes", "favorite_teams", "first_name", "gender", "hometown", "inspirational_people", "installed", "install_type", "is_verified", "languages", "last_name", "link", "locale", "location", "meeting_for", "middle_name", "name", "name_format", "political", "quotes", "payment_pricepoints", "relationship_status", "religion", "security_settings", "significant_other", "sports", "test_group", "timezone", "third_party_id", "updated_time", "verified", "video_upload_limits", "viewer_can_send_gift", "website", "work"}
    public UserProfile(String userId, org.springframework.social.facebook.api.User up) {
        this.userid = userId;
        this.name = up.getName();
        this.firstname = up.getFirstName();
        this.lastname = up.getLastName();
        this.email = up.getEmail();

    }

    private void fixName() {
        // Is the name null?
        if (name == null) {

            // Ok, lets try with first and last name...
            name = firstName;

            if (lastName != null) {
                if (name == null) {
                    name = lastName;
                } else {
                    name += " " + lastName;
                }
            }

            // Try with email if still null
            if (name == null) {
                name = email;
            }

            // Try with username if still null
            if (name == null) {
                name = username;
            }

            // If still null set name to UNKNOWN
            if (name == null) {
                name = "UNKNOWN";
            }
        }
    }

}
