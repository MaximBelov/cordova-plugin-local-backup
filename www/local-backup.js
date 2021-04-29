var localBackup = {
  create: function (data, success, fail) {
    cordova.exec(success, fail, "LocalBackup", "create", [data]);
  },

  read: function (success, fail) {
    cordova.exec(success, fail, "LocalBackup", "read", []);
  },

  remove: function (success, fail) {
    cordova.exec(success, fail, "LocalBackup", "remove", []);
  },

  exists: function (success, fail) {
    cordova.exec(success, fail, "LocalBackup", "exists", []);
  }

};

module.exports = localBackup;
