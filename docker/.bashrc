export SHELL=/bin/bash
export LS_OPTIONS='--color=auto -F --show-control-chars'
export PATH=~/.npm-global/bin:$PATH
eval "`dircolors`"
alias ls='ls $LS_OPTIONS'
alias ll='ls $LS_OPTIONS -lrt'
