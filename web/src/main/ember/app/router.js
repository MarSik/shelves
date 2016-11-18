import Ember from "ember";
import config from "./config/environment";
// global _paq

var Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
  this.route("groups", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:group_id"
    });
  });

  this.route("documents", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:document_id"
    });
  });

  this.route("boxes", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:box_id"
    });
  });

  this.route("lists", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:list_id"
    });
  });

  this.route("footprints", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:footprint_id"
    });

    this.route("new");
  });

  this.route("items", { resetNamespace: true }, function() {
    this.route("show", { path: "/:item_id" }, function () {
      this.route("dependencies");
      this.route("history");
    });

    this.route("new");
  });

  this.route("sources", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:source_id"
    });

    this.route("new");
  });

  this.route("transactions", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:transaction_id"
    });
  });

  this.route("account");

  this.route("types", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:type_id"
    });

    this.route("new");
  });

  this.route("verify", { resetNamespace: true }, function() {
    this.route("show", {
      path: "/:token"
    });
  });

  this.route("cart");
  this.route("stickers");
  this.route("backup");

  this.route("units", { resetNamespace: true }, function() {
    this.route("show", {
        path: "/:unit_id"
    });
    this.route("new");
  });

  this.route("properties", { resetNamespace: true }, function() {
    this.route("show", {
        path: "/:property_id"
    });
    this.route("new");
  });

  this.route("lots", { resetNamespace: true }, function() {
      this.route("show", {
          path: "/:lot_id"
      });
  });
  this.route('search', { resetNamespace: true }, function() {
    this.route('result', {
        path: "/:search_id"
    });
  });
});

/*Router.reopen({
    notifyPiwik: function() {
        _paq.push(['setDocumentTitle', document.title]);
        _paq.push(['trackPageView']);
    }.on('didTransition')
});*/

export default Router;
