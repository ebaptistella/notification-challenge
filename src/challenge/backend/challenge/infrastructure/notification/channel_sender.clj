(ns challenge.infrastructure.notification.channel-sender
  "Protocol for channel send implementations. External layer: send! performs I/O.
   Implementations: sms, email, push. Injectable into the delivery orchestrator.")

(defprotocol ChannelSender
  (send! [this user resolved-message channel]
    "Sends the resolved message to the user on the given channel.
     user: user model, resolved-message: string, channel: channel model.
     Returns :sent or :failed."))
