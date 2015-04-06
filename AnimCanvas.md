# AnimCanvas #

There's no use difference with AnimCanvas over Canvas. The difference is that it uses a surfaceView, rather than a View for the actual Canvas. This should provide better performance when working with several animations on screen. However, a surfaceview tends to take longer to load initially, so only use this if you are making a real-time game with a lot going on animation wise.