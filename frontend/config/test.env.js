'use strict'
const merge = require('webpack-merge')
const devEnv = require('./dev.env')

module.exports = merge(devEnv, {
  NODE_ENV: '"testing"',
  API_ROOT:'"http://10.137.35.134:9527/"',
  HOST:'"test"'
})
