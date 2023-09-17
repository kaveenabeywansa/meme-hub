const express = require('express');
const router = express.Router();
const Controller = require('./controller/post-controller');

router.post('/upvote/:id', function (req, res) {
    Controller.upVote(req.params.id).then(function (data) {
        res.status(data.status).send({ message: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
router.post('/downvote/:id', function (req, res) {
    Controller.downVote(req.params.id).then(function (data) {
        res.status(data.status).send({ message: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
router.post('/', function (req, res) {
    Controller.addPost(req.body).then(function (data) {
        res.status(data.status).send({ message: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
router.get('/', function (req, res) {
    Controller.getPosts().then(function (data) {
        res.status(data.status).send(data.data);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});

module.exports = router;
