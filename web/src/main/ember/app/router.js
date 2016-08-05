import Ember from "ember";
import config from "./config/environment";
// global _paq

var Router = Ember.Router.extend({
  location: config.locationType
});

Router.map(function() {
  this.resource("groups", function() {
    this.route("show", {
      path: "/:group_id"
    });
  });

  this.resource("documents", function() {
    this.route("show", {
      path: "/:document_id"
    });
  });

  this.resource("boxes", function() {
    this.route("show", {
      path: "/:box_id"
    });
  });

  this.resource("lists", function() {
    this.route("show", {
      path: "/:list_id"
    });
  });

  this.resource("footprints", function() {
    this.route("show", {
      path: "/:footprint_id"
    });

    this.route("new");
  });

  this.resource("items", function() {
    this.route("show", { path: "/:item_id" }, function () {
      this.route("dependencies");
      this.route("history");
    });

    this.route("new");
  });

  this.resource("sources", function() {
    this.route("show", {
      path: "/:source_id"
    });

    this.route("new");
  });

  this.resource("transactions", function() {
    this.route("show", {
      path: "/:transaction_id"
    });
  });

  this.route("account");

  this.resource("types", function() {
    this.route("show", {
      path: "/:type_id"
    });

    this.route("new");
  });

  this.resource("verify", function() {
    this.route("show", {
      path: "/:token"
    });
  });

  this.route("cart");
  this.route("stickers");
  this.route("backup");

  this.route("units", function() {
    this.route("show", {
        path: "/:unit_id"
    });
    this.route("new");
  });

  this.route("properties", function() {
    this.route("show", {
        path: "/:property_id"
    });
    this.route("new");
  });

  this.route("lots", function() {
      this.route("show", {
          path: "/:lot_id"
      });
  });
  this.resource('search', function() {
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
