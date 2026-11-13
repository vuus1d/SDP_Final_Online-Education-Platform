"""Build the project report PDF with source excerpts and rendered PlantUML diagrams."""

from pathlib import Path
import re

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.platypus import (
    Image, ListFlowable, ListItem, PageBreak, Paragraph, Preformatted,
    SimpleDocTemplate, Spacer,
)


ROOT = Path(__file__).resolve().parent
PROJECT = ROOT.parent
SOURCE = ROOT / "FINAL_REPORT_DRAFT.md"
DESTINATION = PROJECT / "output" / "pdf" / "FINAL_REPORT_DRAFT.pdf"
UML = PROJECT / "docs" / "uml" / "rendered"
DIAGRAMS = [
    ("Builder", "builder-class.png"),
    ("Factory", "factory-class.png"),
    ("Observer", "observer-class.png"),
    ("Strategy", "strategy-class.png"),
    ("Decorator", "decorator-class.png"),
    ("Facade", "facade-class.png"),
    ("Overall Architecture", "overall-architecture.png"),
    ("End-to-End Sequence", "end-to-end-sequence.png"),
]

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="Cover", parent=styles["Title"], alignment=TA_CENTER,
                          textColor=colors.HexColor("#17365D"), spaceAfter=18))
styles.add(ParagraphStyle(name="Section", parent=styles["Heading1"], textColor=colors.HexColor("#17365D"),
                          spaceBefore=12, spaceAfter=7, keepWithNext=True))
styles.add(ParagraphStyle(name="Subsection", parent=styles["Heading2"], textColor=colors.HexColor("#24527A"),
                          spaceBefore=9, spaceAfter=5, keepWithNext=True))
styles.add(ParagraphStyle(name="BodyReport", parent=styles["BodyText"], leading=14, spaceAfter=7))
styles.add(ParagraphStyle(name="CodeReport", fontName="Courier", fontSize=7.5, leading=9.5,
                          backColor=colors.HexColor("#F2F4F7"), borderColor=colors.HexColor("#D8DEE8"),
                          borderWidth=0.5, borderPadding=7, leftIndent=4, rightIndent=4, spaceAfter=9))
styles.add(ParagraphStyle(name="DiagramTitle", parent=styles["Heading2"], alignment=TA_CENTER,
                          textColor=colors.HexColor("#17365D"), spaceBefore=8, spaceAfter=10))


def inline(text: str) -> str:
    text = re.sub(r"`([^`]+)`", r"<font name='Courier'>\1</font>", text)
    text = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", text)
    return text


story = []
bullets = []
in_code = False
code_lines = []
appendix_started = False
for raw in SOURCE.read_text(encoding="utf-8").splitlines():
    line = raw.rstrip()
    if line.startswith("```"):
        if in_code:
            story.append(Preformatted("\n".join(code_lines), styles["CodeReport"], maxLineLength=105))
            code_lines = []
            in_code = False
        else:
            in_code = True
        continue
    if in_code:
        code_lines.append(line)
        continue
    if line.startswith("## Export and package commands"):
        if bullets:
            story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))
            bullets.clear()
        story.append(PageBreak())
        story.append(Paragraph("Appendix — UML diagrams", styles["Section"]))
        appendix_started = True
        break
    if not line:
        if bullets:
            story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))
            bullets.clear()
        continue
    if line.startswith("# "):
        story.append(Paragraph(inline(line[2:]), styles["Cover"]))
    elif line.startswith("## "):
        story.append(Paragraph(inline(line[3:]), styles["Section"]))
    elif line.startswith("### "):
        if line[4:] == "Java examples":
            story.append(PageBreak())
        story.append(Paragraph(inline(line[4:]), styles["Subsection"]))
    elif line.startswith("- "):
        bullets.append(Paragraph(inline(line[2:]), styles["BodyReport"]))
    else:
        story.append(Paragraph(inline(line), styles["BodyReport"]))
if bullets:
    story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))

for index, (title, filename) in enumerate(DIAGRAMS):
    path = UML / filename
    if not path.exists():
        raise FileNotFoundError(f"Rendered UML image is missing: {path}")
    if index:
        story.append(PageBreak())
    story.append(Paragraph(title, styles["DiagramTitle"]))
    image = Image(str(path), width=165 * mm, height=220 * mm, kind="proportional")
    image.hAlign = "CENTER"
    story.append(image)


def footer(canvas, doc):
    canvas.saveState()
    canvas.setFont("Helvetica", 8)
    canvas.setFillColor(colors.HexColor("#667085"))
    canvas.drawString(20 * mm, 11 * mm, "Online Education Platform — Final Report")
    canvas.drawRightString(A4[0] - 20 * mm, 11 * mm, f"Page {doc.page}")
    canvas.restoreState()


DESTINATION.parent.mkdir(parents=True, exist_ok=True)
doc = SimpleDocTemplate(str(DESTINATION), pagesize=A4, rightMargin=20 * mm, leftMargin=20 * mm,
                        topMargin=18 * mm, bottomMargin=18 * mm,
                        title="Online Education Platform — Final Report",
                        author="Online Education Platform Team")
doc.build(story, onFirstPage=footer, onLaterPages=footer)
print(DESTINATION)
