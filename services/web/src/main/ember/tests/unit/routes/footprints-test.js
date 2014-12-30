import {
  moduleFor,
  test
} from 'ember-qunit';

moduleFor('route:footprints', 'FootprintsRoute', {
  // Specify the other units that are required for this test.
  // needs: ['controller:foo']
});

test('it exists', function() {
  var route = this.subject();
  ok(route);
});
