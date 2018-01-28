## Welcome to MarkdownPad 2 ##

**MarkdownPad** is a full-featured Markdown editor for Windows.

### Built exclusively for Markdown ###

Enjoy first-class Markdown support with easy access to  Markdown syntax and convenient keyboard shortcuts.

Give them a try:

- **Bold** (`Ctrl+B`) and *Italic* (`Ctrl+I`)
- Quotes (`Ctrl+Q`)
- Code blocks (`Ctrl+K`)
- Headings 1, 2, 3 (`Ctrl+1`, `Ctrl+2`, `Ctrl+3`)
- Lists (`Ctrl+U` and `Ctrl+Shift+O`)

### See your changes instantly with LivePreview ###

Don't guess if your [hyperlink syntax](http://markdownpad.com) is correct; LivePreview will show you exactly what your document looks like every time you press a key.

### Make it your own ###

Fonts, color schemes, layouts and stylesheets are all 100% customizable so you can turn MarkdownPad into your perfect editor.

### A robust editor for advanced Markdown users ###

MarkdownPad supports multiple Markdown processing engines, including standard Markdown, Markdown Extra (with Table support) and GitHub Flavored Markdown.

With a tabbed document interface, PDF export, a built-in image uploader, session management, spell check, auto-save, syntax highlighting and a built-in CSS management interface, there's no limit to what you can do with MarkdownPad.


##java8中的stream
    
Java8中，流性能的提升是通过**并行化（parallelism）、惰性（Laziness）和短路操作（short-circuit operations）**来实现的。但它也有一个缺点，在选择流的时候需要非常小心，因为这可能会降低应用程序的性能。

###并行化

在Java的流中，并行化是通过**Fork-Join**原理来实现的。根据Fork-Join原理，系统会将较大的任务切分成较小的子任务（称之为forking），然后并行处理这些子任务以充分利用所有可用的硬件资源，最后将结果合并起来（称之为Join）组成完整的结果。

### 惰性 ###
和spark的transform一样，stream也分为transform和action。一旦最终操作被调用，则开始遍历所有的流，并且相关的函数会逐一应用到流上。

### 短路行为 ###
优化处理流，一旦满足条件，短路操作将会终止处理过程。
