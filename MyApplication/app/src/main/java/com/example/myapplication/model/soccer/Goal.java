package com.example.myapplication.model.soccer;

public class Goal {

    private GoalPost[] posts = new GoalPost[2];

    public Goal(GoalPost.Direction direction, double x, double y, double width, double height) {
        posts[0] = new GoalPost(direction, x, y, height);
        posts[1] = new GoalPost(direction, x + width, y, height);
    }


}
