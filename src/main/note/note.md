#### bug fix
    Int.has Flag* to test bit
    flip when finish putting data into buffer
    SUPER debug mode in (vkk)DEBUG
    The queueFamilyIndex member of each element of pQueueCreateInfos must be unique within pQueueCreateInfos, 
    except that two members can share the same queueFamilyIndex if one is a protected-capable queue and one is not a protected-capable queue
    to destroy an oz object, use cleanup(___::destroy) instead of ___.destroy() to prevent duplicate destruction (at program end)
    
#### Todo list: 
    //use log4j2
    stack-like print style
    
#### note/quote
    It is important that we only try to query for swap chain support after verifying that the extension is available.
    
    different threads can submit work to different queues simultaneously.
    A queue family just describes a set of queues with identical properties
    https://stackoverflow.com/questions/55272626/what-is-actually-a-queue-family-in-vulkan
    