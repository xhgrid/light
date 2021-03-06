import React from 'react';
import RaisedButton from 'material-ui/lib/raised-button';
import WebAPIUtils from '../utils/WebAPIUtils';
import NewsRecentPost from './news/NewsRecentPost';
import BlogRecentPost from './blog/BlogRecentPost';
import ForumRecentPost from './forum/ForumRecentPost';

let Home = React.createClass({
    render: function() {
        return(
            <div>
                <div className="leftColumn">
                    <NewsRecentPost/>
                    <BlogRecentPost/>
                    <ForumRecentPost/>
                </div>
                <div className="rightColumn">
                </div>
            </div>
        )
    }
});

module.exports = Home;
