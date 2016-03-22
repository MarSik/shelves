Setup vim to support Ansible's yaml

curl -fLo ~/.vim/autoload/plug.vim --create-dirs \
    https://raw.githubusercontent.com/junegunn/vim-plug/master/plug.vim


cat >>~/.vimrc <<EOF
call plug#begin('~/.vim/plugged')

" Make sure you use single quotes
Plug 'pearofducks/ansible-vim'

" Add plugins to &runtimepath
call plug#end()
EOF

And then execute the following command from vim

:PlugInstall


