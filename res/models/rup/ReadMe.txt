Welcome to the SimSE Rational Unified Process (RUP) game!

Your assignment is to develop a new computer-based system for a video store company called Movie Corner. The Movie Corner consists of a number of shops, where the customer can rent movies. Today they have 24 shops, 2 warehouses and 123 movie kiosks, distributed in five cities. 

The development contract has a fixed price of $1,950,000 and a fixed delivery date of 800 clock ticks from today. One criterion is also that during the development, the RUP shall be used.

Your goal is to deliver the computer system with complete functionality, within budget and time. Your final score will be based on the completeness of the system, how well you stick to your budget and schedule, and how satisfied the customer is with any intermediate prototypes you may deliver to them.

First, an overview of RUP:

* RUP is an iterative process that consists of four phases: Inception, Elaboration, Construction, and Transition. Within each phase, you will have one or more iterations. Each phase ends with the activity of planning for the next phase.

* IMPORTANT: For a statechart overview of how the process works in this game, see http://www.ics.uci.edu/~emilyo/SimSE/rupstatechart.png

* The key in this game is to follow the steps correctly and make wise tradeoffs in assigning employees with the needed skills to each phase without assigning so many that you go over your budget.

* Inception phase: The goal is to determine the scope and the overall high-level requirements of the project, including the critical use-cases and the overall project plan.

* Elaboration phase: The focus is mostly on determining the rest of the requirements for the project, but also on creating an executable architectural prototype that will serve as an evolvable basis for the final system.

* Construction phase: The focus is on design and implementation, namely, evolving and fleshing out the initial prototype into the first operational product and testing all features thoroughly. User manuals are also developed during this phase.

* Transition phase: The focus is on beta testing, user training, bug fixes, and feature adjustments.

Now, a few hints:

1. Employees are paid only if assigned to a phase. You can assume the ones who are not assigned to that particular phase are working on other projects. Once you have assigned an employee to a phase, you cannot unassign them for that phase, but you can add more employees during a phase.

2. Tools are automatically used if purchased. You may only purchase tools once, so make sure you choose all the ones you need.

Now, a few simplifications that this model makes:

1. The game ends with the beginning of the Transition phase (you can assume this phase takes place after the game is over).

2. Each use-case has exactly one corresponding implemented component.

3. All components are approximately the same size and require the same amount of effort to develop.

4. Unit testing is implied in the development of a component.

5. It makes no difference if the person who implements a component is the same one who integrates it, and/or tests it.

6. Quality is assumed to be acceptable in all cases and is therefore not taken into consideration in this model.

7. There are no iteration assessments, only phase assessments.