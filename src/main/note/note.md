#### bug fix
    Int.has Flag* to test bit
    flip when finish putting data into buffer
    SUPER debug mode in (vkk)DEBUG
    The queueFamilyIndex member of each element of pQueueCreateInfos must be unique within pQueueCreateInfos, 
    except that two members can share the same queueFamilyIndex if one is a protected-capable queue and one is not a protected-capable queue
    to destroy an oz object, use cleanup(___::destroy) instead of ___.destroy() to prevent duplicate destruction (at program end)
    
    buffer need to destroy
    use same semaphore in wait/signal result in wrong behavior (later wait will also triggered)
    
    ##cost one day: data after Stack{ } will atomatically free !
    
    cb on commandpool(family i) submit to correct queue(family i)
    commandbuffer should record in the same thread of allocation
    
    name 'sampler' is reserved in glsl
    
    奇怪的透视视角来源于60 RADIAN 的fov （而不是60 degree）
    
    
#### Todo list: 
    //use log4j2
    stack-like print style
    
#### note/quote
    It is important that we only try to query for swap chain support after verifying that the extension is available.
    
    different threads can submit work to different queues simultaneously.
    A queue family just describes a set of queues with identical properties
    https://stackoverflow.com/questions/55272626/what-is-actually-a-queue-family-in-vulkan
    
    final image location:
        y↓  x→
        
    Recreate the render pass or pipelines? Not necessary (unless the format changes).
    Recreate the swap chain and frame buffers and command buffers? Yes.
    
    window.framebufferSize == currentExtent of getSurfaceCapabilitiesKHR (test)
    
    use one ticker (tick in FrameLoop) and delay fix period (0.1ms for example)
    
    Unlike timeline semaphores, fences or events, the act of waiting for a binary semaphore also unsignals that semaphore.
    
    Submission can be a high overhead operation, and applications should attempt to batch work together into as few calls to vkQueueSubmit as possible.
    
    An image will eventually be acquired if the number of images that the application has currently acquired (but not yet presented) is less than or equal to the difference between the number of images in swapchain and the value of VkSurfaceCapabilitiesKHR::minImageCount. 
    If the number of currently acquired images is greater than this, vkAcquireNextImageKHR should not be called; if it is, timeout must not be UINT64_MAX.
    
    By default, all calls to functions that take VmaAllocator as first parameter are safe to call from multiple threads simultaneously because they are synchronized internally when needed.
    
    depth buffer range [0, 1]
    
    If any of the sets being bound include dynamic uniform or storage buffers, then pDynamicOffsets includes one element for each array element in each dynamic descriptor type binding in each set. Values are taken from pDynamicOffsets in an order such that all entries for set N come before set N+1; within a set, entries are ordered by the binding numbers in the descriptor set layouts; and within a binding array, elements are in order. dynamicOffsetCount must equal the total number of dynamic descriptors in the sets being bound.
    
    apply inverse model matrix to view pos (let camera into model space) to do selection.
    
    10000 Icosphere have 65fps 