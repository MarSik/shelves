package org.marsik.elshelves.ember;

import java.util.Map;

/**
 * See https://github.com/emberjs/data/pull/1371 for descriptions of
 * ember links.
 *
 * Absolute paths start with a leading / and will use only the provided host provided to the adapter.
 *
 * { "posts": [{ "id": 1, "links": { "comments": "/posts/1/comments" }}] }
 * Relative paths construct an URL from the record the relationship is embedded in:
 *
 * { "posts": [{ "id": 1, "links": { "comments": "comments" }}] }
 * Full URLs links will pass through the link transparently:
 *
 * { "posts": [{ "id": 1, "links": { "comments": "http://example.com/posts/1/comments" }}] }
 * All of the above examples are equivalent to each other.
 */
public interface EmberLinks {
    Map getLinks();
}
