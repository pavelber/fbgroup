package org.fbgroups.lucene
/**
 * Created by Pavel on 10/5/2015.
 */
interface ISearcher {
    Set<FBPost> search(String username, String text, Date from, Date t)
}