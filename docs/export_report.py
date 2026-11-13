"""Create a plain, reproducible PDF draft from FINAL_REPORT_DRAFT.md using ReportLab."""

from pathlib import Path
from xml.sax.saxutils import escape

from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, ListFlowable, ListItem


ROOT = Path(__file__).resolve().parent
source = ROOT / "FINAL_REPORT_DRAFT.md"
destination = ROOT.parent / "output" / "pdf" / "FINAL_REPORT_DRAFT.pdf"
destination.parent.mkdir(parents=True, exist_ok=True)
styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="CoverTitle", parent=styles["Title"], alignment=TA_CENTER, spaceAfter=14))
styles.add(ParagraphStyle(name="H1Draft", parent=styles["Heading1"], spaceBefore=12, spaceAfter=7))
styles.add(ParagraphStyle(name="H2Draft", parent=styles["Heading2"], spaceBefore=9, spaceAfter=5))
styles.add(ParagraphStyle(name="BodyDraft", parent=styles["BodyText"], leading=14, spaceAfter=7))


def inline(text: str) -> str:
    # Preserve inline code as bold text and escape all other Markdown punctuation safely.
    chunks = text.split("`")
    return "".join(("<b>" + escape(part) + "</b>") if i % 2 else escape(part) for i, part in enumerate(chunks))


story = []
bullets = []
for raw in source.read_text(encoding="utf-8").splitlines():
    line = raw.strip()
    if not line:
        continue
    if line.startswith("# "):
        story.append(Paragraph(inline(line[2:]), styles["CoverTitle"]))
    elif line.startswith("## "):
        story.append(Paragraph(inline(line[3:]), styles["H1Draft"]))
    elif line.startswith("### "):
        story.append(Paragraph(inline(line[4:]), styles["H2Draft"]))
    elif line.startswith("- "):
        bullets.append(Paragraph(inline(line[2:]), styles["BodyDraft"]))
    else:
        if bullets:
            story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))
            bullets.clear()
        if line.startswith("---") or line.startswith("|"):
            continue
        story.append(Paragraph(inline(line), styles["BodyDraft"]))
if bullets:
    story.append(ListFlowable([ListItem(item) for item in bullets], bulletType="bullet", leftIndent=16))
    bullets.clear()

doc = SimpleDocTemplate(str(destination), pagesize=A4, rightMargin=20 * mm, leftMargin=20 * mm,
                        topMargin=18 * mm, bottomMargin=18 * mm, title="Online Education Platform - Report Draft")
doc.build(story)
print(destination)
