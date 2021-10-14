package com.chrc.gradle;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * @author : chrc
 * date   : 10/12/21  10:24 AM
 * desc   :
 */
class CreateFilePlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.task("CreateFileTask").doFirst(new Action<Task>() {
            @Override
            public void execute(Task task) {
                System.out.println("CreateFilePlugin");
            }
        });
    }

}
