/* global require, module */
var EmberApp = require('ember-cli/lib/broccoli/ember-app');

module.exports = function(defaults) {
  var app = new EmberApp(defaults, {
    vendorFiles: {
        'handlebars.js': null
    },
    'ember-cli-foundation-sass': {
        'fastclick': true,
        'foundationJs': 'all'
    },
    gzip: {
        extensions: ['js', 'css', 'svg'],
        keepUncompressed: true
    }
    // Add options here
  });

  // Use `app.import` to add additional libraries to the generated
  // output files.
  //
  // If you need to use different assets in different
  // environments, specify an object as the first parameter. That
  // object's keys should be the environment name and the values
  // should be the asset to use in that environment.
  //
  // If the library that you are including contains AMD or ES6
  // modules that you would like to import into your application
  // please specify an object with the list of modules as keys
  // along with the exports of each module as its value.

  app.import('bower_components/qrcodejs/qrcode.js');
  app.import('bower_components/webcamjs/webcam.js');
  app.import('bower_components/webcamjs/webcam.swf', {
    destDir: 'assets'
  });

  return app.toTree();
};
