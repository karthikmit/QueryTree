package com.querytree;

/**
 * QueryTree interface.
 *
 * QueryTree supports following object path queries with eval method.
 *
 *      rootKey->childKey1->childKey2
 *      rootKey->childKey1->childKey2->field
 *      rootKey->childKey1->childKey2[index]->field
 *      rootKey->childKey1->childKey2[*] - Returns the list of objects.
 */
public interface QueryTree {
    Object eval(String query);
}
