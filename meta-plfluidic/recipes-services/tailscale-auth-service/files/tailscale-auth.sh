#!/bin/sh

echo "nameserver 8.8.8.8" > /etc/resolv.conf

# Wait for the tailscale daemon to be ready
until [ -S /run/tailscale/tailscaled.sock ]; do
  sleep 2
done

# Check if currently authenticated
if tailscale status | grep -q "Logged out"; then
    echo "Provisioning Tailscale..."
    tailscale up --authkey=@TS_AUTH_KEY@ --accept-dns=false
else
    echo "Tailscale is already online."
fi