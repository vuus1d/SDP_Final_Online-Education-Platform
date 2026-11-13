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
    SimpleDocTemplate,
)


ROOT = Path(__file__).resolve().parent
PROJECT = ROOT.parent
SOURCE = ROOT / "FINAL_REPORT_DRAFT.md"
DESTINATION = PROJECT / "output" / "pdf" / "FINAL_REPORT_DRAFT.pdf"

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="Cover", parent=styles["Title"], alignment=TA_CENTER,
                          fontSize=29, leading=36, textColor=colors.HexColor("#17365D"),
                          spaceBefore=80, spaceAfter=22))
styles.add(ParagraphStyle(name="CoverSubtitle", parent=styles["Heading1"], alignment=TA_CENTER,
                          fontSize=17, leading=22, textColor=colors.HexColor("#24527A"),
                          spaceAfter=48))
styles.add(ParagraphStyle(name="CoverMeta", parent=styles["BodyText"], alignment=TA_CENTER,
                          fontSize=12, leading=19, spaceAfter=5))
styles.add(ParagraphStyle(name="Section", parent=styles["Heading1"], textColor=colors.HexColor("#17365D"),
                          spaceBefore=12, spaceAfter=7, keepWithNext=True))
styles.add(ParagraphStyle(name="Subsection", parent=styles["Heading2"], textColor=colors.HexColor("#24527A"),
                          spaceBefore=9, spaceAfter=5, keepWithNext=True))
styles.add(ParagraphStyle(name="BodyReport", parent=styles["BodyText"], leading=14, spaceAfter=7))
styles.add(ParagraphStyle(name="CodeReport", fontName="Courier", fontSize=7.5, leading=9.5,
                          backColor=colors.HexColor("#F2F4F7"), borderColor=colors.HexColor("#D8DEE8"),
                          borderWidth=0.5, borderPadding=7, leftIndent=4, rightIndent=4, spaceAfter=9))


def inline(text: str) -> str:
    text = re.sub(r"`([^`]+)`", r"<font name='Courier'>\1</font>", text)
    text = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", text)
    return text


story = []
bullets = []
in_code = False
code_lines = []
on_cover = True
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
    if line == "<!-- PAGEBREAK -->":
        if bullets:
            story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))
            bullets.clear()
        story.append(PageBreak())
        on_cover = False
        continue
    if not line:
        if bullets:
            story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))
            bullets.clear()
        continue
    if line.startswith("# "):
        story.append(Paragraph(inline(line[2:]), styles["Cover"]))
    elif line.startswith("## "):
        if line.startswith("## 6.") or line.startswith("## 7."):
            story.append(PageBreak())
        story.append(Paragraph(inline(line[3:]), styles["Section"]))
    elif line.startswith("### "):
        if (line[4:].endswith("UML") and line[4:] != "Builder UML"):
            story.append(PageBreak())
        story.append(Paragraph(inline(line[4:]), styles["Subsection"]))
    elif line.startswith("- "):
        bullets.append(Paragraph(inline(line[2:]), styles["BodyReport"]))
    elif line.startswith("!["):
        if bullets:
            story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))
            bullets.clear()
        match = re.fullmatch(r"!\[([^\]]*)\]\(([^)]+)\)", line)
        if not match:
            raise ValueError(f"Invalid Markdown image reference: {line}")
        image_path = ROOT / match.group(2)
        if not image_path.exists():
            raise FileNotFoundError(f"Report image is missing: {image_path}")
        image = Image(str(image_path), width=165 * mm, height=218 * mm, kind="proportional")
        image.hAlign = "CENTER"
        story.append(image)
    else:
        if on_cover:
            style = styles["CoverSubtitle"] if line.startswith("**Final Project Report**") else styles["CoverMeta"]
            story.append(Paragraph(inline(line), style))
        else:
            story.append(Paragraph(inline(line), styles["BodyReport"]))
if bullets:
    story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))

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
