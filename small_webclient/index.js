var express = require('express'),
    app = express();

app.set('view engine', 'pug');
app.use(express.static('public'));

app.get('/', function(req, res) {
    res.render('index', {});
});

app.listen(8083, function () {
    console.log("http://0.0.0.0:8083");
});
