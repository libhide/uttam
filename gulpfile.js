'use strict';

var gulp = require('gulp');
var browserSync = require('browser-sync').create();
var sass = require('gulp-sass');
var rename = require('gulp-rename');
var minify = require('gulp-minify-css');
var prefix = require('gulp-autoprefixer');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var del = require('del');

gulp.task('rebuild', function () {
    browserSync.reload();
});

gulp.task('browser-sync', ['sass', 'minifyJS'], function() {

    browserSync.init({
        server: {
            baseDir: "./"
        },
        notify: false
    });
});

/**
 * Concat JS into one file
 */
gulp.task('concatJS', function() {
    return gulp.src([
            'js/jquery.min.js',
            'js/simplebox.js',
            'js/index.js'
        ])
        .pipe(concat('site.js'))
        .pipe(gulp.dest('./js'));
});

/**
 * Minify the concat'd JS
 */
gulp.task('minifyJS', ['concatJS'], function() {
    var ret = gulp.src('js/site.js')
        .pipe(uglify({
            onError: browserSync.notify
        }))
        .pipe(rename('site.min.js'))
        .pipe(gulp.dest('./js'))
        .pipe(browserSync.reload({stream:true}));

    del(['.js/site.js']);
    return ret;
});

// Compile sass into CSS
gulp.task('sass', function() {
    return gulp.src("./sass/*.scss")
        .pipe(sass())
        .pipe(prefix(['last 15 versions', '> 1%', 'ie 8', 'ie 7']))
        .pipe(gulp.dest('./css'))
        .pipe(minify())
        .pipe(rename('style.min.css'))
        .pipe(gulp.dest('./css'))
        .pipe(browserSync.stream());
});

gulp.task('watch', function () {
    gulp.watch(['./js/index.js'], ['minifyJS']);
    gulp.watch(["./sass/*.scss"], ['sass']);
    gulp.watch(['./*.html'], ['rebuild']);
});

gulp.task('default', ['browser-sync', 'watch']);
