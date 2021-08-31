SimSE Incremental Model

Model Author: Alex Baker

This is an open-ended process simulation. Your project is broken up into 4 modules, each of which can have its own requirements progress, design, implementation, etc. In the end, you will want to integrate all of these modules and submit a single, unified project to the customer, but you may submit partial builds consisting of a subset of the modules along the way. This model allows you to follow an incremental lifecycle model, but there are a wide variety of possible approaches to completing this project. Your progress might resemble a waterfall model, a rapid prototype model, a spiral model, or take on the manner of another approach altogether.

There are several important values that each module has:

Value: The amount by which the module influences your final score (does not work in this version)

Inflexibility: The degree to which inaccuracy will reduce your score for this module.

Changability: The relative number of ticks that it will take for a change to take place on this module. Each change will reduce the module’s accuracy.

Accuracy: This represents how accurately the project represents what the customer wants. Keep in mind that this is orthogonal to the completeness of the project. In order to have a good score, your modules must be both complete and accurate.


The following actions can be taken on each module:

Risk analysis: Reveals the value, inflexibility and changability of the module when completed, with a small amount of error.

Difficulty Analysis: Reveals the difficulty of each of the actions for this module, with a small amount of error.

Requirements: Increases ease of design, increases the project’s accuracy and reduces the changability

Design: Increases ease of implementation and integration, makes it easier to evolve code

Implementation: Determines the module’s overall completeness

Evolution: Increases accuracy, but decreases design. You may need to do this after a customer makes changes

Integrate: You must integrate modules to submit a multiple-module build to the customer

Start over: Removes most of the progress from most of your phases, but leaves any difficulty decreases you have earned intact

Submission to user: Many of the module’s attributes are revealed, the difficulty of all actions is reduced, changability is greatly reduced and the requirements difficulty and changability of all *other* modules in the project are reduced as well. The usefulness of this action depends on the module’s implementation completeness and accuracy. When you are asked to provide “other modules” when initiating this action, but sure to select all the other modules you are not submitting, this will give them the bonus described above

Submission of a multiple-module build: If you have fully integrated more than one module, you may submit them as a single large build, which carries even greater benefits than a single module submission

Note: This model ignores several aspects of the software process, including tools, budget concerns and personnel issues.

Try not to get frustrated if the customer's changes confound your progress, overcoming this is one of the model's primary challenges. Try lots of different approaches and discover what works!
