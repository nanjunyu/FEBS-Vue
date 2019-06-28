'use strict'
let HOST = process.argv.splice(2)[0] || 'prod';
module.exports = {
  NODE_ENV: '"production"',
  // API_ROOT:'"http://www.invs.com:9527/"',
  HOST:'"'+HOST+'"'
}
