SHELL = /bin/bash

COMPILED_FIGURES=compiledfigures
TEXFILES=resource client resourceallocation token

paper:
	@rm -rf ../${COMPILED_FIGURES}
	@mkdir ../${COMPILED_FIGURES}
	@for i in ${TEXFILES}; do pdflatex $$i.tex; pdfcrop $$i.pdf; mv $$i-crop.pdf ../${COMPILED_FIGURES}/; done
	@echo "done generating figures...";
	@rm -f *.log *.pdf *.aux
clean:
	@rm -f *.log *.pdf *.aux *.gz *.dvi
