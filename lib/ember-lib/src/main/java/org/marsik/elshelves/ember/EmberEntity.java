package org.marsik.elshelves.ember;

import java.util.UUID;

/**
 * See http://springember.blogspot.com.au/2014/08/using-ember-data-restadapter-with.html for full explanation.
 *
 * The entity classes have to:
 *
 * - classes have to be annotated with @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", resolver = _Object_IdResolver.class)
 * - annotate all 1:1 foreign relationship getters with @JsonIdentityReference(alwaysAsId = true) Category getCategory()
 * - annotate setter for 1:1 relationships -- @JsonSetter void setCategory(Long id)
 *
 * - annotate all 1:n foreign relationship getters with @JsonIdentityReference(alwaysAsId = true) List<Category> getCategories()
 * - have an entities/{id}/posts controller mapping that returns the posts
 * - return the map for 1:n fetchers using getLinks() {"posts": "posts"}
 * - annotate all 1:n foreign relationship setters -- @JsonSetter void setPosts(List<Post> p)
 *
 * All entities returned from controller handlers have to be created using the EmberModel.Builder,
 * see the following example:
 *
 *         return new EmberModel.Builder(blog)
 *             .sideLoad(Post.class, blog.getPosts())
 *             .addMeta("totalRecords", 100)
 *             .build();
 */
public interface EmberEntity {
    UUID getId();
}
