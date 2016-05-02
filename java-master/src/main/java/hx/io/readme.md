With the earliest versions of Java (1.0-1.3), there was a lack of comprehensive I/Osupport.
Namely, developers faced the following problems when developing applications that required I/O support:
■ There was no concept of buffers or channel abstractions for data, meaning lots
of detailed low-level programming for developers.
■ I/O operations would get blocked, limiting scalability.
■ There  was  limited  character  set  encoding  support,  leading  to  a  plethora  of
hand-coded solutions to support certain types of hardware.
■ There was no regular expression support, making data manipulation difficult.


In order to solve these problems, Java began to implement nonblocking I/Osupport,
as well as other I/Ofeatures, to help developers deliver faster, more reliable I/Osolutions.
There have been two major advancements:
■ The introduction of nonblocking I/Oas part of Java 1.4
■ An overhaul of nonblocking I/O as part of Java 7

Under the guise of JSR-51, nonblocking input/output (NIO) was added to Java when
version 1.4 was released in 2002. The following broad feature set was added at that
time, turning Java into an attractive language to use for server-side development:
■ Buffer and channel abstraction layers for I/O operations
■ The ability to encode and decode character sets
■ An interface that can map files to data held in memory
■ The capability to write nonblocking I/O
■ A new regular expression library based on the popular Perl implementation


NIO was definitely a great step forward, but Java developers still faced hardship when
programming I/O. In particular, the support for handling files and directories on a
filesystem was still inadequate. The java.io.File class, at the time, had some annoying limitations:
■ It did not deal with filenames consistently across all platforms.
■ It failed to have a unified model for file attributes (for  example, modeling read/write access).
■ It was difficult to traverse directories.
■ It didn’t allow you to use platform- or OS-specific features.
■ It didn’t support nonblocking operations for filesystems (only support for nonblocking socket io).


JSR-203(led by Alan Bateman) was formed to address the preceding limitations as
well as to provide support for some of the new I/Oparadigms in modern hardware
and software. JSR-203has become what we now know as the NIO.2 APIin Java 7. It had
three major goals, which are detailed in JSR-203, section 2.1 (http://jcp.org/en/jsr/
detail?id=203):
1 A new filesystem interface that supports bulk access to file attributes, escape to
filesystem-specific APIs, and a service-provider interface for pluggable filesystem
implementations.
2 An API for asynchronous (as opposed to polled, nonblocking) I/Ooperations
on both sockets and files.
3 The completion of the socket-channel functionality defined in JSR-51, including the
addition of support for binding, option configuration, and multicast datagrams.

Summary: 
Java IO: two low-level, blocking and limited encoding support
Java NIO: Buffer and Channel abstraction; support fot nonblocking socket io (which is
 		stream-oriented) based on Selector (kernel notify and then thread wake up,
 		syschronous Reactor pattern); file io is still blocking
Java NIO.2: support for nonblocking file io; asynchronous Proactor pattern (Callback style
		and Future style) 