deepcut 0 pwl_ellip figure 1 cla set gca fontsize 18 m 200 x 1 m plot x f_save 1 m hold on plot x lm 1 m plot x 1 x end cvx_optval cvx_optval k axis 0 m 8 4 text x 27 f_save 27 u text x 27 lm 27 l text 2 cvx_optval fs set gca yticklabel set gca xticklabel a b c d e 0 500 1000 1500 2000 xlabel k print deps pwl_ell_ul1 eps figure 2 cla set gca fontsize 18 m 2000 x 1 m semilogy x f_best 1 m cvx_optval axis 0 m 1e 4 1e0 xlabel k ylabel fbest fmin print deps pwl_ell_ul2 eps figure 3 cla set gca fontsize 18 m 2000 x 1 m plot x u 1 m hold on plot x l 1 m plot x 1 x end cvx_optval cvx_optval k axis 0 m 3 3 text x 300 u 300 u text x 300 l 300 l text 20 cvx_optval fs set gca yticklabel set gca xticklabel a b c d e xlabel k print deps pwl_ell_ul3 eps
