const mongoose = require('../dbschema/dbconfig');
const PostsSchema = mongoose.model('Posts');

var Controller = function () {
    this.addPost = function (data) {
        return new Promise(function (resolve, reject) {
            var Post = PostsSchema({
                author: data.author,
                text: data.text,
                imageLink: data.imageLink,
                likes: 0,
            });
            Post.save().then(function () {
                resolve({ status: 200, message: "Successfully Added !" });
            }).catch(function (reason) {
                reject({ status: 404, message: "Error: " + reason });
            })
        });
    };
    this.getPosts = function () {
        return new Promise(function (resolve, reject) {
            PostsSchema.find().exec().then(function (value) {
                resolve({ status: 200, data: value });
            }).catch(function (reason) {
                reject({ status: 404, message: "Error: " + reason });
            })
        })
    };
    this.upVote = function (id) {
        return new Promise(function (resolve, reject) {
            PostsSchema.findOne({ _id: id }).exec().then(function (value) {
                var tempVal = value.likes;
                tempVal += 1;
                value.likes = tempVal;
                value.save();
                resolve({ status: 200, message: "Successfully updated!" });
            }).catch(function (reason) {
                reject({ status: 401, message: "Post not found !" });
            })
        })
    };
    this.downVote = function (id) {
        return new Promise(function (resolve, reject) {
            PostsSchema.findOne({ _id: id }).exec().then(function (value) {
                var tempVal = value.likes;
                if (tempVal > 0) {
                    tempVal -= 1;
                    value.likes = tempVal;
                    value.save();
                }
                resolve({ status: 200, message: "Successfully updated!" });
            }).catch(function (reason) {
                reject({ status: 401, message: "Post not found !" });
            })
        })
    };
};

module.exports = new Controller();
