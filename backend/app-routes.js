const express = require('express');
var Routes = express.Router();

const PostRoutes = require('./modules/posts/post-router');

Routes.use('/posts/', PostRoutes);
Routes.use('/', function(req, res) {
    res.sendFile(__dirname+'/home.html');
});

module.exports = Routes;