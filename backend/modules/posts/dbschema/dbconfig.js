var mongoose = require('mongoose');
const Schema = mongoose.Schema;

const Posts = new Schema({
    author: String,
    text: String,
    imageLink: String,
    likes: Number
});

mongoose.model('Posts', Posts);

mongoose.connect('mongodb://localhost:27017/memehub', { useNewUrlParser: true }, function (err) {
    if (err) {
        console.log(err);
        process.exit(-1);
    }
    console.log('Connected to MongoDB !');
});
module.exports = mongoose;
