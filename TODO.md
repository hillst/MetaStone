#TODO

1) Set a time limit for policy rollout (90 seconds I believe?)

2) Efficiency metrics for simulations, how many simulations per second etc

3) Threaded Policy rollout

4) Implement hidden information, as in, the agent shouldn't know anything about the other deck ( a bit more complex )



- Implement deck classifiers to predict an opponents deck for future rollout

- It might be a good idea to move the policy swapping(not like algorithm policy swap) to the policies themself instead of the simulator constructor

- Another problem is that when an action is taken, a lot of the available actions have already been seen. It might be worth saving their Qvalues instead of doing more rollout, although it could be possible to do both? Also by redoing rollout it lets us take into account the new action.
